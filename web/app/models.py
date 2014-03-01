from app import db

ROLE_USER = 0
ROLE_ADMIN = 1

class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	mail = db.Column(db.String(256), default='')
	name = db.Column(db.String(128), default='')
	role = db.Column(db.SmallInteger, default = ROLE_USER)

class Visits(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	user_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
	beacon_id = db.Column(db.Integer, db.ForeignKey("beacon.id"), nullable=False)
	role = db.Column(db.SmallInteger, default = ROLE_USER)
	time_entered = db.Column(db.DateTime)
	time_left = db.Column(db.DateTime)

class Beacon(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	beacon_identifier = db.Column(db.String(128), default='')
	location = db.Column(db.String(64), default='')
	picture = db.Column(db.String(128), default='') # url to picture location
