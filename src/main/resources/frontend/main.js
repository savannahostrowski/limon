$(document).ready(function () {
    var id = window.location.href.split("/").pop();
    var video = document.getElementById("vid");
    var source = document.createElement("source");

    source.src = "/movies/The%20Goonies.mp4";
    source.type = "video/mp4";
    video.appendChild(source);

});

