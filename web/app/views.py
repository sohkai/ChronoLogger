from flask import request, session
from flask import Flask, render_template, jsonify, request, redirect, url_for
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json
from models import User
from models import Visits
from models import Beacon

@app.route('/logout')
def logout():
	session['chrono_token'] = ''
	return redirect('/login')

@app.route('/login')
def login():
	session['chrono_token'] = 'fake'
	return render_template('login.html')

@app.route('/dashboard')
def dashboard():
	return render_template('index.html')

def is_loggedin():
	return 'chrono_token' in session and session['chrono_token'] != ''

@app.route('/')
def home():
	# If logged in, redirect to main dashboard, otherwise
	# redirect to login page
	if is_loggedin():
		return render_template('index.html')
	else:
		return redirect('/login')

#################################
# API for Tim
#################################

@app.route('/get_for_user/<id>')
def get_for_user(id = None):
	try:
		user = User.query.filter(User.id == id).one()
	except:
		# No user was found
		return err404()

	visits = Visits.query.filter(Visits.user_id == user.id).all()
	
	visits_to_return = []
	for visit in visits:
		beacon = Beacon.query.filter(Beacon.id == visit.beacon_id).one()

		try:
			time_entered = visit.time_entered.strftime('%s')
		except:
			time_entered = ''

		try:
			time_left = visit.time_left.strftime('%s')
		except:
			time_left = ''

		visits_to_return.append({'time_entered': time_entered, 'time_left': time_left, \
			'location': beacon.location, 'beacon_string': beacon.beacon_identifier})

	return json.dumps({'visits': visits_to_return, 'name': user.name})

@app.route('/get_for_all_today')
def get_for_all_today():
	return get_for_all(time=datetime.datetime.utcnow().strftime('%s'))

@app.route('/get_for_all/<time>')
def get_for_all(time=None):
	date = datetime.datetime.fromtimestamp(int(time))
	data = []

	users = User.query.all()
	for user in users:
		visits = Visits.query.filter(Visits.user_id == user.id).all()
		collected_visits = []
		
		for visit in visits:
			beacon = Beacon.query.filter(Beacon.id == visit.beacon_id).one()

			if visit.time_entered.strftime("%Y:%m:%d") != date.strftime("%Y:%m:%d"):
				continue # This isn't today

			try:
				time_entered = visit.time_entered.strftime('%s')
			except:
				time_entered = ''

			try:
				time_left = visit.time_left.strftime('%s')
			except:
				time_left = ''

			collected_visits.append({'time_entered': time_entered, 'time_left': time_left, \
				'location': beacon.location, 'beacon_string': beacon.beacon_identifier})
		data.append({'name': user.name, 'visits': collected_visits})

	return json.dumps({'data': data})

#################################
# API for Android app
#################################

@app.route('/checkin/<mail>/<beacon_string>', methods = ['POST'])
def checkin(mail=None, beacon_string=None):
	try:
		user = User.query.filter(User.mail == mail).one()
	except:
		# No user was found
		return err404()

	try:
		beacon = Beacon.query.filter(Beacon.beacon_identifier == beacon_string).one()
	except:
		# No beacon was found
		return err404()

	# Create a new visits object
	visit = Visits(user_id=user.id, beacon_id=beacon.id, time_entered=datetime.datetime.utcnow(), time_left=None)

	db.session.add(visit)
	db.session.commit()

	return "OK"

@app.route('/leave/<mail>/<beacon_string>', methods = ['POST'])
def leave(mail=None, beacon_string=None):
	try:
		user = User.query.filter(User.mail == mail).one()
	except:
		# No user was found
		return err404()

	try:
		beacon = Beacon.query.filter(Beacon.beacon_identifier == beacon_string).one()
	except:
		# No beacon was found
		return err404()

	# Get the corresponding visit object to complete date left
	visit = Visits.query.filter(Visits.user_id == user.id and Visits.beacon_id == beacon.id and Visits.time_left == None).one()
	visit.time_left = datetime.datetime.utcnow()

	db.session.add(visit)
	db.session.commit()

	return "OK"

@app.route('/getvisits/<mail>', methods = ['GET'])
def get_visits(mail=None):
	try:
		user = User.query.filter(User.mail == mail).one()
	except:
		# No user was found
		return err404()

	visits = Visits.query.filter(Visits.user_id == user.id).all()
	
	visits_to_return = []
	for visit in visits:
		beacon = Beacon.query.filter(Beacon.id == visit.beacon_id).one()

		try:
			time_entered = visit.time_entered.strftime('%s')
		except:
			time_entered = ''

		try:
			time_left = visit.time_left.strftime('%s')
		except:
			time_left = ''

		visits_to_return.append({'time_entered': time_entered, 'time_left': time_left, \
			'location': beacon.location, 'beacon_string': beacon.beacon_identifier})

	return json.dumps({'visits': visits_to_return, 'name': user.name})

@app.route('/register_new_device/<beacon_string>/<location_string>', methods = ['POST'])
def register_new_device(beacon_string=None, location_string=None):
	beacon = Beacon(beacon_identifier=beacon_string, location=location_string)
	db.session.add(beacon)
	db.session.commit()

	return "OK"

#################################
# Error handlers
#################################

@app.errorhandler(404)
def page_not_found(e):
	return err404()

@app.errorhandler(500)
def page_not_found(e):
	return err500()

def err404():
	return render_template('404.html'), 404

def err500():
	return render_template('500.html'), 500
