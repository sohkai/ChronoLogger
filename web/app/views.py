from flask import request, session
from flask import Flask, render_template, jsonify, request, redirect, url_for
from app import app, models, db
from flask.ext.sqlalchemy import SQLAlchemy
import datetime
import os, json

@app.route('/login')
def login():
	return render_template('login.html')

@app.route('/dashboard')
def dashboard():
	return render_template('dashboard.html')

@app.route('/view/<user_id>')
def view():
	# View activity of a particular user
	return render_template('view.html')

@app.route('/add')
def add():
	# Admins can add people to user list or admin list
	return render_template('add.html')

@app.route('/')
def home():
	# If logged in, redirect to main dashboard, otherwise
	# redirect to login page
	return "Hello world!"

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
