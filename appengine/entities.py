from google.appengine.ext import db

"""
Data type representing a single data point
"""
class Point(db.Model):
    location = db.GeoPtProperty(required=True)                          # GPS locations
    angle = db.FloatProperty(required=True)                             # Direction of movement in radians (+pi to -pi)
    wifi = db.BooleanProperty(required=True)                            # Whether wifi was obtained eventually
    time = db.IntegerProperty(default=None, required=False)             # if wifi == true
    user_id = db.StringProperty(required=True)                          # Unique ID for each user

"""
Prediction for a certain location and movement obtained via machine learning
"""
class Prediction(db.Model):
    location = db.GeoPtProperty(required=True)                          # GPS locations
    angle = db.FloatProperty(required=True)                             # Direction of movement in radians (+pi to -pi)
    wifi = db.BooleanProperty(required=True)                            # Whether wifi will be obtained eventually
    time = db.IntegerProperty(default=None, required=False)             # if wifi == true