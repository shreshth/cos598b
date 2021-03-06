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
        bearing = str.split(str(self.request.get('bearing')),',')
        speed = str.split(str(self.request.get('speed')),',')
        accuracy = str.split(str(self.request.get('accuracy')),',')
        time = str.split(str(self.request.get('time')),',')
        timestamp = str.split(str(self.request.get('timestamp')),',')
        # Read user id
        user_id = self.request.get('user_id')
        # All arrays must have the same size
        if ((len(lat) != len (lng)) or (len(lat) != len (bearing)) or (len(lat) != len (time)) or (len(lat) != len (timestamp)) or (len(lat) != len (speed)) or (len(lat) != len (accuracy))):
            self.error(400)
        # Add each data point to the database
        for i in range(len(lat)):
            location = db.GeoPt(lat[i], lng[i])
            # time >= 0 means wifi was obtained eventually
            wifi = (int(time[i]) >= 0)
            # Store data point
            point = entities.Point(location=location, bearing=float(bearing[i]), speed=float(speed[i]), accuracy=float(accuracy[i]), wifi=wifi, time=int(time[i]), timestamp=int(timestamp[i]), user_id = user_id)
            point.put()

application = webapp.WSGIApplication([('/.*', AddData)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()