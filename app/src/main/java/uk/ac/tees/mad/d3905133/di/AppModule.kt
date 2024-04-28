package uk.ac.tees.mad.d3905133.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.tees.mad.d3905133.data.TripzyDao
import uk.ac.tees.mad.d3905133.data.TripzyDatabase
import uk.ac.tees.mad.d3905133.data.TripzyRepository
import uk.ac.tees.mad.d3905133.data.TripzyRepositoryImpl
import uk.ac.tees.mad.d3905133.ui.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore.getInstance()

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun providesHttpClient(): HttpClient {
        return HttpClient(Android) {
            // Logging
            install(Logging) {
                level = LogLevel.ALL
            }
            install(DefaultRequest) {
                url(BASE_URL)
                header("X-RapidAPI-Key", "bacaa75f45msh9e7679f64cea41ep114e00jsnacd32ec613d9")
                header("X-RapidAPI-Host", "travel-advisor.p.rapidapi.com")
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        explicitNulls = false
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    @Singleton
    @Provides
    fun provideDao(db: TripzyDatabase) = db.getTripzyDao()

    @Singleton
    @Provides
    fun provideApiService(httpClient: HttpClient, dao: TripzyDao, firebaseAuth: FirebaseAuth, firestore: FirebaseFirestore, @ApplicationContext context: Context): TripzyRepository =
        TripzyRepositoryImpl(httpClient, dao, firebaseAuth, firestore, context)


    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(
            app,
            TripzyDatabase::class.java,
            "tripzy_db"
        ).build()

}