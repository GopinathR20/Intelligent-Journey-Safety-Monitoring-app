import { Component, OnInit } from '@angular/core';
import { IonContent, IonButton, IonItem, IonLabel, IonInput } from '@ionic/angular/standalone'; 
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SafetyService } from '../services/safety.service';
import * as L from 'leaflet';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  standalone: true,
  imports: [CommonModule, FormsModule, IonContent, IonButton, IonItem, IonLabel, IonInput],
})
export class HomePage implements OnInit {
  fromLocation: string = 'Current Location';
  toLocation: string = '';
  private map!: L.Map;
  private userMarker!: L.Marker;

  // Marked as readonly to satisfy SonarLint
  constructor(private readonly safetyService: SafetyService) {}

  ngOnInit() {
    this.initMap();
    console.log('Journey Safety Monitor Active');
  }

  initMap() {
    // Standard starting coordinates for demo
    this.map = L.map('map', { zoomControl: false }).setView([12.9786, 77.364], 15);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);
    
    this.userMarker = L.marker([12.9786, 77.364]).addTo(this.map)
      .bindPopup('Tracking Active').openPopup();
  }

  startJourney() {
    if (!this.toLocation) return;
    
    console.log(`Analyzing path to: ${this.toLocation}`);
    // Triggers the intelligence loop in the background service
    this.safetyService.startTrackingLoop(this.toLocation); 
  }
}