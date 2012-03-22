from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

import entities

class HomePage(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.out.write('Coming soon...')

class AddData(webapp.RequestHandler):
    def post(self):
        lat = self.request.get('lat')
        lng = self.request.get('lat')
        lat_delta = self.request.get('lat_delta')
        lng_delta = self.request.get('lat_delta')
        time = int(self.request.get('time'))
        user_id = self.request.get('user_id')
        location = db.GeoPt(lat, lng)
        delta = db.GeoPt(lat_delta, lng_delta)
        point = entities.Point(location=location, delta=delta, time=time, user_id = user_id)
        point.put()

application = webapp.WSGIApplication([('/', HomePage),
                                      ('/add_data', AddData)], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()