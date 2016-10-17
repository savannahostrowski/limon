var { Router,
      Route,
      IndexRoute,
      IndexLink,
      Link } = ReactRouter;
      var browserHistory = ReactRouter.browserHistory;
    var destination = document.querySelector("#content");
	var Movie = React.createClass({

		getInitialState: function() {
			return {
				movies: []
			};
		},

		 componentDidMount: function() {
		    var _this = this;
                this.serverRequest =
                    axios
                        .get("/movies.json")
                        .then(function(result) {
                           _this.setState({
                                movies: result.data
                               });
                            })
         },
		componentWillUnmount: function () {
			this.serverRequest.abort();
		},
		render: function() {
			return (
				<div className="pure-g lst">
                    {this.state.movies.map(function(movie) {
                    var moviePath = "/movie/watch/" + movie.imdbID;
                    var movieTitle = (movie.Title).toUpperCase();
                        return(
                            <div className="pure-u-1 pure-u-md-1-2 pure-u-lg-1-3 pure-u-xl-1-3 movie" key={movie.imdbID}>
                                <div className="imgdiv">
                                    <a href={moviePath}>
                                        <img src={movie.Poster}></img>
                                    </a>
                                  </div>
                                <div className="description">
                                    <h3 className="title">{movieTitle} ({movie.Year})</h3>
                                    <h5 className="genre">{movie.Genre}</h5>
                                    <h5 className="plot">{movie.Plot}</h5>
                                    <h5 className="actors">Actors: {movie.Actors}</h5>
                                    <h5 className="director"> Director: {movie.Director}</h5>
                                </div>
                            </div>
                        );
                    })}
				</div>
			)
		}
	});
	ReactDOM.render(<Movie/>, destination);