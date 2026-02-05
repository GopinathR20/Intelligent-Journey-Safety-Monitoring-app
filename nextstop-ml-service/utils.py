import numpy as np
from math import radians, cos, sin, asin, sqrt, atan2, degrees

def calculate_haversine_distance(lat1, lon1, lat2, lon2):
    """Calculates Distance Deviation (delta d) in meters."""
    R = 6371000  # Earth radius in meters
    phi1, phi2 = radians(lat1), radians(lat2)
    dphi = radians(lat2 - lat1)
    dlambda = radians(lon2 - lon1)
    a = sin(dphi / 2)**2 + cos(phi1) * cos(phi2) * sin(dlambda / 2)**2
    return 2 * R * asin(sqrt(a))

def calculate_bearing(lat1, lon1, lat2, lon2):
    """Calculates Directional Bearing (theta) in degrees."""
    phi1, phi2 = radians(lat1), radians(lat2)
    d_lambda = radians(lon2 - lon1)
    y = sin(d_lambda) * cos(phi2)
    x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(d_lambda)
    # Result is 0-360 degrees
    return (degrees(atan2(y, x)) + 360) % 360

def get_4d_vector(curr_lat, curr_lon, prev_lat, prev_lon):
    """Constructs the vector: [lat, lon, delta_d, theta]"""
    delta_d = calculate_haversine_distance(prev_lat, prev_lon, curr_lat, curr_lon)
    theta = calculate_bearing(prev_lat, prev_lon, curr_lat, curr_lon)
    return [curr_lat, curr_lon, delta_d, theta]