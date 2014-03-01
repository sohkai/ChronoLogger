from flask import Flask
from flask.ext.sqlalchemy import SQLAlchemy
import os

UPLOAD_FOLDER = os.path.join(os.getcwd(), 'app/static/uploads');
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])

app = Flask(__name__)

app.secret_key = 'wenfui2s8923fbiuASDASDYdn23diu23dbiu23bdn23oidb3y2vd3'
app.config.from_object('config')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['ALLOWED_EXTENSIONS'] = ALLOWED_EXTENSIONS

db = SQLAlchemy(app)

from app import views, models
