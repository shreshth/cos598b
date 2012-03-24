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
    addpoints(map);
}