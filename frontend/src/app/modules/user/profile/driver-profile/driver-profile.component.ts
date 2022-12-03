import { Component, OnInit } from '@angular/core';
import { faCar, faPaperPlane, faMobileRetro } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-driver',
  templateUrl: './driver-profile.component.html'
})
export class DriverProfileComponent implements OnInit {
  faCar = faCar;
  faPaperPlane = faPaperPlane;
  faMobileRetro = faMobileRetro;
  
  isDriverActive = false;

  constructor() { }

  ngOnInit(): void {
  }

}
