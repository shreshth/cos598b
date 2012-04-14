from google.appengine.ext import db

"""
Data type representing a single data point
"""
class Point(db.Model):
    location = db.GeoPtProperty(required=True)                          # GPS locations
    bearing = db.FloatProperty(required=True)                           # Direction of movement in radians (+pi to -pi)
    speed = db.FloatProperty(required=True)                             # Speed of movement
    accuracy = db.FloatProperty(required=True)                          # Accuracy of GPS movement
    wifi = db.BooleanProperty(required=True)                            # Whether wifi was obtained eventually
    time = db.IntegerProperty(default=None, required=False)             # if wifi == true
    timestamp = db.IntegerProperty(required=True)                       # the timestamp (time since 1970) in ms
    user_id = db.StringProperty(required=True)                          # Unique ID for each user

"""
Prediction for a certain location and movement obtained via machine learning
"""
class Prediction(db.Model):
    location = db.GeoPtProperty(required=True)                          # GPS locations
    angle = db.FloatProperty(required=True)                             # Direction of movement in radians (+pi to -pi)
    wifi = db.BooleanProperty(required=True)                            # Whether wifi will be obtained eventually
    time = db.IntegerProperty(default=None, required=False)             # if wifi == true