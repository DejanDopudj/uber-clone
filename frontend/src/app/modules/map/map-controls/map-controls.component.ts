import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-map-controls',
  templateUrl: './map-controls.component.html',
  styleUrls: ['./map-controls.component.css']
})
export class MapControlsComponent implements OnInit {
  @Output() clear: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  clearMarkers = (): void => {
    this.clear.emit();
  }

}
