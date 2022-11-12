import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements AfterViewInit {
  private map: any;
  private control: any;
  public summary: any;

  private initMap(): void {
    L.Marker.prototype.options.icon = L.icon({
      iconUrl: './assets/icons/pinpoint-marker.png',
      iconSize:     [30, 45],
      iconAnchor:   [15, 45],
    });
  
    this.map = L.map('map', {
      center: [ 45.254326, 19.827178 ],
      zoom: 15
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    });
    tiles.addTo(this.map);

    this.map.on('click', this.addMarker); 

    const that = this;
    this.control = L.Routing.control({
      lineOptions: {styles: [{color: '#006D5B', weight: 4}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      altLineOptions: {styles: [{color: '#8aa1ad', weight: 7}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      showAlternatives: true,
      addWaypoints: true,
      waypoints: [],
      autoRoute: true,
      routeWhileDragging: true,
    })
    .on('routesfound', function(e) {
      that.summary = e.routes[0].summary;
    })
    .on('routeselected', function(e) {
      that.summary = e.route.summary;
    })
    .addTo(this.map);
  }

  constructor() { }

  ngAfterViewInit(): void {
    this.initMap();
  }

  addMarker = (e: any) => {
    this.control.setWaypoints([...this.control.getPlan().getWaypoints().filter((x: L.Routing.Waypoint) => x.latLng), e.latlng]);
  }

  clearMarkers(): void {
    this.control.setWaypoints([]);
    this.summary = null;
  }

}
