import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Geolocation } from '@capacitor/geolocation';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SafetyService {
  private readonly apiUrl = 'http://localhost:8081/api/safety'; // Java Backend

  constructor(private readonly http: HttpClient) {}

  // This fixes the 'startTrackingLoop' error
  async startTrackingLoop(destination: string) {
    setInterval(async () => {
      try {
        const position = await Geolocation.getCurrentPosition();
        const payload = {
          userId: 'test_user_gopin',
          lat: position.coords.latitude,
          lon: position.coords.longitude,
          destination: destination // Send the 'To' location to the AI
        };
        
        this.checkSafety(payload).subscribe(res => {
          console.log('Safety Status:', res.status);
        });
      } catch (err) {
        console.error('GPS error', err);
      }
    }, 10000); // Check every 10 seconds
  }

  checkSafety(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/check`, data);
  }
}