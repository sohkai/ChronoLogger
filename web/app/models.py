from app import db

class User(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	linkedin_id = db.Column(db.String(128), default='')
	publicUrl = db.Column(db.String(128), default='')
	authtoken = db.Column(db.String(256), default='')
	mail = db.Column(db.String(256), default='')
	pictureUrl = db.Column(db.String(256), default='')
	name = db.Column(db.String(128), default='')
	def serialize(self):
		ret = {
			"linkedin_id": self.linkedin_id,
			"publicUrl": self.publicUrl,
			"mail": self.mail,
			"pictureUrl": self.pictureUrl,
			"name": self.name,
		}
		return ret

class Product(db.Model):
	id = db.Column(db.Integer, primary_key=True)
	user_id = db.Column(db.Integer, db.ForeignKey("user.id"), nullable=False)
	category = db.Column(db.String(20), default='')
	descAndTitle = db.Column(db.String(1100), default='')
	desc = db.Column(db.String(1024), default='')
	title = db.Column(db.String(32), default='')
	price = db.Column(db.String(128), default='Free')
	selltype = db.Column(db.Integer, default=0)
	view = db.Column(db.Integer, default=0)
	timestamp = db.Column(db.DateTime)
	imglist = db.Column(db.String(1600),default='')
	imgcount = db.Column(db.Integer,default=0)
	
	def image(product):
		return product.imglist.split(',')[0]
