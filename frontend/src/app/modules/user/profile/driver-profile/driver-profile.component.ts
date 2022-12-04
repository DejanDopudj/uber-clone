import { Component, OnInit, Input } from '@angular/core';
import { IconDefinition, faBars } from '@fortawesome/free-solid-svg-icons';
import { Driver } from 'src/app/shared/models/driver.model';

enum DriverProfileView {
  Details = "details",
  Reviews = "reviews"
}

@Component({
  selector: 'app-driver-profile',
  templateUrl: './driver-profile.component.html'
})
export class DriverProfileComponent implements OnInit {
  @Input() driver!: Driver;
  
  _selectedView: DriverProfileView = DriverProfileView.Details; 

  faBars: IconDefinition = faBars;
  
  showDropdown: boolean = false;

  constructor() { }

  ngOnInit(): void {
  }

  set selectedView(value: string) {
    this._selectedView = value as DriverProfileView;
  }

  get selectedView() {
    return this._selectedView;
  }

}
