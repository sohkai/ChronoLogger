from flask import request, session
from flask import Flask, render_template, jsonify, request, redirect, url_for
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json
from models import Product
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

@app.route('/view/<user_id>')
def view():
	# View activity of a particular user
	return render_template('view.html')

@app.route('/add')
def add():
	# Admins can add people to user list or admin list
	return render_template('add.html')

def is_loggedin():
	return 'chrono_token' in session and session['chrono_token'] != ''

@app.route('/')
def home():
	# If logged in, redirect to main dashboard, otherwise
	# redirect to login page
	if is_loggedin():
		return redirect('/dashboard')
	else:
		return redirect('/login')

#################################
# API for Android app
#################################

@app.route('/checkin', methods = ['POST'])
def checkin():
	mail = str(request.get('mail'))
	beacon_string = str(request.get('beacon'))

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

	return "" # OK

@app.route('/leave', methods = ['POST'])
def leave():
	mail = str(request.get('mail'))
	beacon_string = str(request.get('beacon'))

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

	return ""

@app.route('/getvisits', methods = ['GET'])
def get_visits():
	mail = str(request.get('mail'))
	return "TODO"

@app.route('/register_new_device', methods = ['POST'])
def register_new_device():
	mail = str(request.get('mail'))
	beacon_string = str(request.get('beacon'))
	return "TODO"

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
