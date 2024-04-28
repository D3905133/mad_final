package uk.ac.tees.mad.d3905133.data

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.storage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.util.InternalAPI
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.d3905133.domain.ApiResult
import uk.ac.tees.mad.d3905133.domain.GetDetailModel
import uk.ac.tees.mad.d3905133.domain.GetPlacePhotos
import uk.ac.tees.mad.d3905133.domain.GetRecommendedPlace
import uk.ac.tees.mad.d3905133.domain.LocationResult
import uk.ac.tees.mad.d3905133.domain.Recents
import uk.ac.tees.mad.d3905133.domain.Resource
import uk.ac.tees.mad.d3905133.domain.UserResponse
import java.util.UUID
import javax.inject.Inject


interface TripzyRepository {

    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>

    suspend fun saveUser(email: String?, userId: String?)
    fun confirmUser(): Flow<Resource<Boolean>>
    fun getCurrentUser(): Flow<Resource<UserResponse>>

    fun addUserDetail(
        name: String,
        address: String,
        image: ByteArray
    ): Flow<Resource<String>>

    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
    suspend fun getSearchAutoComplete(string: String): Flow<ApiResult<LocationResult>>

    suspend fun getPlaceDetail(id: Int): Flow<ApiResult<GetDetailModel>>

    suspend fun getPlacePhotos(id: Int): Flow<ApiResult<GetPlacePhotos>>

    suspend fun getRecommendedPlace(): Flow<ApiResult<GetRecommendedPlace>>

    suspend fun getRankedPlace(): Flow<ApiResult<GetRecommendedPlace>>

    fun getAllRecentSearch(): Flow<List<Recents>>

    suspend fun addRecentSearch(recent: Recents)

    suspend fun deleteRecentSearch(recent: Int)
}

class TripzyRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val tripzyDao: TripzyDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val context: Context
) : TripzyRepository {

    override fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override fun confirmUser(): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            val user = firebaseAuth.currentUser
            user?.sendEmailVerification()?.await()
            emit(Resource.Success(true))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }

    }


    override fun getCurrentUser(): Flow<Resource<UserResponse>> = callbackFlow {
        trySend(Resource.Loading())
        val currentUserUid = firebaseAuth.currentUser?.uid
        if (currentUserUid != null) {
            firestore.collection("users").document(currentUserUid).get()
                .addOnSuccessListener { mySnapshot ->
                    if (mySnapshot.exists()) {
                        val data = mySnapshot.data

                        if (data != null) {
                            val userResponse = UserResponse(
                                key = currentUserUid,
                                item = UserResponse.CurrentUser(
                                    name = data["name"] as String? ?: "",
                                    email = data["email"] as String? ?: "",
                                    address = data["address"] as String? ?: "",
                                    profileImage = data["image"] as String? ?: ""
                                )
                            )

                            trySend(Resource.Success(userResponse))
                        } else {
                            trySend(Resource.Error(message = "No data found in Database"))

                            println("No data found in Database")
                        }
                    } else {
                        trySend(Resource.Error(message = "No data found in Database"))
                        println("No data found in Database")
                    }
                }.addOnFailureListener { e ->
                    Log.d("ERRor", e.toString())
                    trySend(Resource.Error(message = e.toString()))
                }
        } else {
            trySend(Resource.Error(message = "User not signed up"))
        }
        awaitClose {
            close()
        }
    }

    override fun addUserDetail(
        name: String,
        address: String,
        image: ByteArray
    ): Flow<Resource<String>> =
        callbackFlow {
            trySend(Resource.Loading())
            val storageRef = Firebase.storage.reference
            val uuid = UUID.randomUUID()
            val imagesRef = storageRef.child("images/$uuid")
            val currentUserUid = firebaseAuth.currentUser?.uid

            val uploadTask =
                image.let {
                    imagesRef.putBytes(it)
                }

            uploadTask.addOnSuccessListener {
                imagesRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["name"] = name
                        map["address"] = address
                        map["image"] = uri.toString()
                        if (currentUserUid != null) {
                            firestore.collection("users")
                                .document(currentUserUid)
                                .set(map, SetOptions.merge())
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        trySend(Resource.Success("Updated Successfully.."))
                                    }
                                }
                                .addOnFailureListener { e ->
                                    trySend(Resource.Error(message = e.message))
                                }
                        } else {
                            trySend(Resource.Error(message = "User not logged in"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Resource.Error(message = "Updating user failed Successfully: $it"))
                    }
            }.addOnFailureListener {
                trySend(Resource.Error(message = "Image upload failed Successfully: $it"))
            }
            awaitClose { close() }
        }

    override fun registerUser(email: String, password: String): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            // Add user to Firestore with username
            val userId = authResult.user?.uid
            saveUser(userId = userId, email = email)
            emit(Resource.Success(authResult))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override suspend fun saveUser(
        email: String?,
        userId: String?
    ) {
        if (userId != null) {
            val userMap = hashMapOf(
                "email" to email,
            )
            firestore.collection("users").document(userId).set(userMap).await()
        }
    }

    override fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>> {
        return flow {
            emit(Resource.Loading())
            val result = firebaseAuth.signInWithCredential(credential).await()
            emit(Resource.Success(result))
        }.catch {
            emit(Resource.Error(it.message.toString()))
        }
    }

    override suspend fun getSearchAutoComplete(string: String): Flow<ApiResult<LocationResult>> =
        flow {
            emit(ApiResult.Loading())
            try {
                emit(
                    ApiResult.Success(
                        httpClient.get("/locations/v2/auto-complete") {
                            parameter("query", string)
                            parameter("lang", "en_US")
                            parameter("units", "km")
                        }.body()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(e.message ?: "Something went wrong"))
            }
        }

    @OptIn(InternalAPI::class)
    override suspend fun getPlaceDetail(id: Int): Flow<ApiResult<GetDetailModel>> =
        flow {
            emit(ApiResult.Loading())
            try {
                emit(
                    ApiResult.Success(
                        httpClient.get("/attractions/get-details") {
                            parameter("location_id", id)
                            parameter("lang", "en_US")
                        }.body()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(e.message ?: "Something went wrong"))
            }
        }

    override suspend fun getPlacePhotos(id: Int): Flow<ApiResult<GetPlacePhotos>> =
        flow {
            emit(ApiResult.Loading())
            try {
                emit(
                    ApiResult.Success(
                        httpClient.get("/photos/list") {
                            parameter("location_id", id)
                            parameter("lang", "en_US")
                        }.body()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(e.message ?: "Something went wrong"))
            }
        }

    override suspend fun getRecommendedPlace(): Flow<ApiResult<GetRecommendedPlace>> =
        flow {
            emit(ApiResult.Loading())
            try {
                emit(
                    ApiResult.Success(
                        httpClient.get("/attractions/list") {
                            parameter("location_id", 186338)
                            parameter("lang", "en_US")
                            parameter("lunit", "km")
                            parameter("sort", "recommended")
                        }.body()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(e.message ?: "Something went wrong"))
            }
        }

    override suspend fun getRankedPlace(): Flow<ApiResult<GetRecommendedPlace>> =
        flow {
            emit(ApiResult.Loading())
            try {
                emit(
                    ApiResult.Success(
                        httpClient.get("/attractions/list") {
                            parameter("location_id", 186340)
                            parameter("lang", "en_US")
                            parameter("lunit", "km")
                            parameter("sort", "ranking")
                        }.body()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiResult.Error(e.message ?: "Something went wrong"))
            }
        }

    //Getting Recent Searches
    override fun getAllRecentSearch(): Flow<List<Recents>> = tripzyDao.getAllRecentSearch()
    override suspend fun addRecentSearch(recent: Recents) = tripzyDao.addRecentSearch(recent)
    override suspend fun deleteRecentSearch(recent: Int) = tripzyDao.deleteRecentSearch(recent)

}