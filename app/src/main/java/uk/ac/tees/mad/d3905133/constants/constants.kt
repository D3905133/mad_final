package uk.ac.tees.mad.d3905133.constants

import uk.ac.tees.mad.d3905133.R
import uk.ac.tees.mad.d3905133.presentation.navigation.BottomNavigationScreens

val bottomNavigationItems = listOf(
    BottomNavigationScreens.Explore,
    BottomNavigationScreens.Search,
    BottomNavigationScreens.Account,
)

const val ServerClient = "739483138257-gij5juekrn1bb7lbl4bmbvpcqopec1hr.apps.googleusercontent.com"

data class Category(
    val imgResId: Int, val title: String
)

val categoryList = listOf(
    Category(
        imgResId = R.drawable.lake, title = "Beach"
    ), Category(
        imgResId = R.drawable.adventure, title = "Adventure"
    ), Category(
        imgResId = R.drawable.historic, title = "Historic"
    ), Category(
        imgResId = R.drawable.nature, title = "Nature"
    ), Category(
        imgResId = R.drawable.trip, title = "Trip"
    ), Category(
        imgResId = R.drawable.lake, title = "Lake"
    )
)


val Cities = listOf(
    City(name = "London", link = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTQvD-OjYayRDMj_QIuhNl4Db-446qFaHyrpz8OPgsl8Q&s"),
    City(
        name = "Edinburgh",
        link = "https://assets.isu.pub/document-structure/230315110809-3e230920af89888e1fba194dd0e56596/v1/c36728093f01aea5a00a9575025d137a.jpeg"
    ),
    City(
        name = "Manchester",
        link = "https://cdn.britannica.com/42/116342-050-5AC41785/Manchester-Eng.jpg"
    ),
    City(
        name = "Birmingham",
        link = "https://a.cdn-hotels.com/gdcs/production170/d336/affcb1c1-50f7-4783-8570-e41bd4e38a5c.jpg?impolicy=fcrop&w=800&h=533&q=medium"
    ),
    City(
        name = "Glasgow",
        link = "https://static.independent.co.uk/s3fs-public/thumbnails/image/2018/05/04/16/glasgow-main.jpg?width=1200"
    ),
    City(
        name = "Liverpool",
        link = "https://www.thisisanfield.com/wp-content/uploads/liverpool-city-collage.jpeg"
    ),
    City(name = "Bristol", link = "https://media.cntraveller.com/photos/611bef51a106ea5ed309abea/4:3/w_2664,h_1998,c_limit/bristol-gettyimages-505612113.jpg"),
    City(name = "York", link = "https://assets-global.website-files.com/62f61fc1509cff1793e3fd35/62f664bb992cf40db9c0bf6b_york-resize.jpg"),
    City(
        name = "Oxford",
        link = "https://www.sarahlawrence.edu/media/study-abroad-and-exchange/england/oxford/oxford_main.jpg"
    ),
    City(
        name = "Cardiff",
        link = "https://cdn.britannica.com/79/74879-050-05312339/keep-Cardiff-Castle-Wales.jpg/"
    )
)

data class City(val name: String, val link: String)

