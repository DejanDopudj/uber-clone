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

    this.map.on('click', addMarker); 

    let control = L.Routing.control({
      lineOptions: {styles: [{color: '#006D5B', weight: 4}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      altLineOptions: {styles: [{color: '#8aa19d', weight: 6}], extendToWaypoints: true, missingRouteTolerance: 0.1},
      showAlternatives: true,
      addWaypoints: true,
      waypoints: [],
      autoRoute: true,
      routeWhileDragging: true,
    })
    .on('routesfound', function(e) {
    })
    .addTo(this.map);

    function addMarker(e: any){
      control.setWaypoints([...control.getPlan().getWaypoints().filter(x => x.latLng), e.latlng]);
    }
  }

  constructor() { }

  ngAfterViewInit(): void {
    this.initMap();
  }

}
