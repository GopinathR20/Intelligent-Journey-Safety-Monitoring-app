from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.svm import OneClassSVM
import numpy as np
import pandas as pd
import joblib
import os
from utils import get_4d_vector

app = FastAPI(title="NextStopML Intelligence Service")

# 1. Configuration & Model Path
MODEL_PATH = "data/safety_model.pkl"
TRAIN_DATA_PATH = "data/normal_route.csv"

# Initialize OCSVM
model = OneClassSVM(kernel='rbf', nu=0.05, gamma=0.1)

class JourneyUpdate(BaseModel):
    curr_lat: float
    curr_lon: float
    prev_lat: float
    prev_lon: float

# 2. Intelligence Logic: Training and Persistence
def initialize_intelligence():
    global model
    if os.path.exists(MODEL_PATH):
        model = joblib.load(MODEL_PATH)
        print("--- Intelligence Engine: Pre-trained model loaded. ---")
    elif os.path.exists(TRAIN_DATA_PATH):
        df = pd.read_csv(TRAIN_DATA_PATH)
        training_features = []
        for i in range(1, len(df)):
            vector = get_4d_vector(
                df.iloc[i]['lat'], df.iloc[i]['lon'],
                df.iloc[i-1]['lat'], df.iloc[i-1]['lon']
            )
            training_features.append(vector)
        
        model.fit(training_features)
        os.makedirs('data', exist_ok=True)
        joblib.dump(model, MODEL_PATH)
        print(f"--- Intelligence Engine: Trained on {len(training_features)} points and saved. ---")
    else:
        # Fallback to mock data if no files exist
        sample_data = [[13.1145, 80.1110, 0.5, 90.0], [13.1140, 80.1112, 0.6, 92.0]]
        model.fit(sample_data)
        print("--- Intelligence Engine: Initialized with mock safety data. ---")

initialize_intelligence()

@app.get("/")
def health_check():
    return {"status": "online", "system": "NextStopML"}

@app.post("/verify-safety")
async def verify_safety(data: JourneyUpdate):
    vector = get_4d_vector(data.curr_lat, data.curr_lon, data.prev_lat, data.prev_lon)
    vector_reshaped = np.array(vector).reshape(1, -1)
    
    prediction = model.predict(vector_reshaped)[0]
    status = "normal" if prediction == 1 else "anomaly"
    
    return {
        "status": status,
        "parameters": {
            "latitude": vector[0],
            "longitude": vector[1],
            "distance_deviation": round(vector[2], 4),
            "bearing_theta": round(vector[3], 2)
        }
    }
def check_deviation(current_lat, current_lon, planned_path):
    # Logic to calculate distance between current GPS and the expected line
    # If distance > 500 meters, return "anomaly"
    is_off_track = calculate_distance_from_path(current_lat, current_lon, planned_path)
    
    if is_off_track:
        return {"status": "anomaly", "reason": "Path Deviation Detected"}
    return {"status": "normal"}

from geopy.geocoders import Nominatim
from geopy.distance import geodesic

geolocator = Nominatim(user_agent="nextstop_safety_app")

def calculate_distance_from_path(curr_lat, curr_lon, to_location_name):
    try:
        # 1. Convert the name (e.g., "Bangalore") to Coordinates
        location = geolocator.geocode(to_location_name)
        if not location:
            return False # Can't find place, assume safe for now
            
        target_point = (location.latitude, location.longitude)
        current_point = (curr_lat, curr_lon)
        
        # 2. Calculate the real distance
        distance = geodesic(current_point, target_point).meters
        print(f"Distance to {to_location_name}: {distance}m")
        
        # 3. If user is > 500m away from the destination/path, it's an anomaly
        return distance > 500 
    except Exception as e:
        print(f"Geocoding error: {e}")
        return False