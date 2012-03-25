from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

import entities

class AddData(webapp.RequestHandler):
    def get(self):
        self.post()
    def post(self):
        lat = str.split(str(self.request.get('lat')),',')
        lng = str.split(str(self.request.get('lng')),',')
        lat_delta = str.split(str(self.request.get('lat_delta')),',')
        lng_delta = str.split(str(self.request.get('lng_delta')),',')
        time = str.split(str(self.request.get('time')),',')
        user_id = self.request.get('user_id')
        if ((len(lat) != len (lng)) or (len(lat) or len (lat_delta)) or (len(lat) != len (lng_delta)) or (len(lat) != len (time))):
            self.error(400)
        for i in range(len(lat)):
            location = db.GeoPt(lat[i], lng[i])
            delta = db.GeoPt(lat_delta[i], lng_delta[i])
            point = entities.Point(location=location, delta=delta, time=int(time[i]), user_id = user_id)
            point.put()

application = webapp.WSGIApplication([('/.*', AddData)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()