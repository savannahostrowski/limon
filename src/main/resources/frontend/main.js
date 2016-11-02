$(document).ready(function () {
    function getParameterByName(name, url) {
        if (!url) {
          url = window.location.href;
        }
        name = name.replace(/[\[\]]/g, "\\$&");
        var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }

    var id = getParameterByName("id");
    var jsonPage = "/api/" + id  + "/movie.json";
    function getJSON() {
        return $.getJSON(jsonPage);
    }

    var elem;
    getJSON().then(function(data) {
        elem = data[0];
        var title = elem.Title;
        var appended = false;
        var h1 = $("<h1 class='watchTitle'>" + title + "</h1>");
        $(h1).insertBefore("#video");

        var path = elem.Path;
        var arr = path.split("/");
        var end = arr.slice(arr.length - 2, arr.length);
        var relPath = "/" + end[0] + "/" + end[1];
        var video = document.getElementById("my_video_html5_api");
        var source = document.createElement("source");

        source.src = relPath;
        source.type = "video/mp4";
        video.appendChild(source);

    });
});

