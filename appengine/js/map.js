var map;
var points;

function initialize() {
    var mapOptions = {
        center: new google.maps.LatLng(40.346545,-74.654425),
        zoom: 16,
        minZoom: 13,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        mapTypeControl: false,
        streetViewControl: false,
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
    addpoints();
}

function addpoints() {
    var r = 0.0001;
    for (var i = 0; i < points.length; i++) {
        var point = points[i];
        var lat = point[0];
        var lng = point[1];
        var angle = point[2];
        var wifi = point[3];
        lng_delta = (Math.cos(angle)) * r;
        lat_delta = (Math.sin(angle)) * r;
        var location1 = new google.maps.LatLng(lat, lng);
        var location2 = new google.maps.LatLng(lat + lat_delta, lng + lng_delta);
        var path = [location1, location2];
        var color = (wifi) ? "#2500DB" : "#FF0000";
        (new google.maps.Polyline({
            path: path,
            strokeColor: color,
            strokeOpacity: 1.0,
            strokeWeight: 2,
        })).setMap(map);
    }
}