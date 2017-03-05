# Popular Movies Android App
Android app that shows a list of the top rated and most popular movies by using `themoviedb.org` JSON 
API. 

Implements libraries as `Picasso` for image downloads and caching, `Volley` for HTTP request.

On the Main Screen `MainActivity` there is a RecyclerView that shows all movies.

![Image of Popular Movies Main Activity](https://gnstudenko.github.io/images/PopularMovies/MainActivity.png)

The movies can be sorted by `Top Rated`, `Most Popular` or `My Favorites`

![Image of Popular Movies Sort By](https://gnstudenko.github.io/images/PopularMovies/SortBy.png)

The Favorites movies are being saved on a `SQLite Database` that is accessed through a 
`Content Provider` when the user clicks on the Star button on the `DetailsActivity` the movie details
gets saved on the database and made available offline as well as the images.

On the `DetailsActivity` we show all the Movie details including Poster, Rating, Release Date, Synopsis,
Youtube trailers and Reviews.

![Image of Popular Movies Details Activity](https://gnstudenko.github.io/images/PopularMovies/details.png)

Youtube reviews thumbnails are being downloaded and shown on screen, on click they will lunch an 
`Implicit Intent` so the user can decide if launch it with the Youtube app or any browser.

![Image of Popular Movies Details Activity Youtube Trailers](https://gnstudenko.github.io/images/PopularMovies/trailers.png)

Below the Youtube trailers are the reviews.

![Image of Popular Movies Details Activity Reviews](https://gnstudenko.github.io/images/PopularMovies/reviews.png)