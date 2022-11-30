import { Component, OnInit } from '@angular/core';
import { faCar, faPaperPlane, faMobileRetro } from '@fortawesome/free-solid-svg-icons';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  faCar = faCar;
  faPaperPlane = faPaperPlane;
  faMobileRetro = faMobileRetro;
  faChevronLeft = faChevronLeft;
  
  isDriverActive = false;
  constructor() { }

  ngOnInit(): void {
  }

}
