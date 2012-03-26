from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from math import atan2

import entities

class AddData(webapp.RequestHandler):
    def get(self):
        self.post()
    def post(self):
        # Read in comma seperated values and split them into arrays
        lat = str.split(str(self.request.get('lat')),',')
        lng = str.split(str(self.request.get('lng')),',')
        lat_delta = str.split(str(self.request.get('lat_delta')),',')
        lng_delta = str.split(str(self.request.get('lng_delta')),',')
        time = str.split(str(self.request.get('time')),',')
        # Read user id
        user_id = self.request.get('user_id')
        # All arrays must have the same size
        if ((len(lat) != len (lng)) or (len(lat) or len (lat_delta)) or (len(lat) != len (lng_delta)) or (len(lat) != len (time))):
            self.error(400)
        # Add each data point to the database
        for i in range(len(lat)):
            location = db.GeoPt(lat[i], lng[i])
            # Calculate angle
            angle = atan2(float(lat_delta[i]), float(lng_delta[i])) 
            # time >= 0 means wifi was obtained eventually
            wifi = (time >= 0)
            # Store data point
            point = entities.Point(location=location, angle=angle, wifi=wifi, time=int(time[i]), user_id = user_id)
            point.put()

application = webapp.WSGIApplication([('/.*', AddData)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()