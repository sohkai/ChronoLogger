from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
import os

app = Flask(__name__)

app.secret_key = 'wenfui2s8923fbiuASDASDYdn23diu23dbiu23bdn23oidb3y2vd3'
app.config.from_object('config')

db = SQLAlchemy(app)

from app import views, models
