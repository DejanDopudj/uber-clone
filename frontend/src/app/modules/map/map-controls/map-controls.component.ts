import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

interface RideSummary {
  totalDistance: number,
  totalTime: number,
}

@Component({
  selector: 'app-map-controls',
  templateUrl: './map-controls.component.html',
  styleUrls: ['./map-controls.component.css']
})
export class MapControlsComponent implements OnInit {
  @Input() summary!: RideSummary;
  @Output() clear: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }

  clearMarkers = (): void => {
    this.clear.emit();
  }

}
