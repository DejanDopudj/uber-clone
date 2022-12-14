import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IconDefinition, faChevronRight, faChevronLeft, faCircle, faFlagCheckered, faStop, faPlus, faXmark } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-order-menu',
  templateUrl: './order-menu.component.html',
  styleUrls: ['./order-menu.component.css']
})
export class OrderMenuComponent implements OnInit {
  @Input() waypoints: any[] = [];
  @Output() stopAdded: EventEmitter<string> = new EventEmitter<string>();
  @Output() stopRemoved: EventEmitter<Number> = new EventEmitter<Number>();

  faChevronRight: IconDefinition = faChevronRight;
  faChevronLeft: IconDefinition = faChevronLeft;
  faCircle: IconDefinition = faCircle;
  faStop: IconDefinition = faStop;
  faPlus: IconDefinition = faPlus;
  faFlagCheckered: IconDefinition = faFlagCheckered;
  faXmark: IconDefinition = faXmark;

  isOpened: boolean = true;
  isAddNewStopOpened: boolean = true;
  newStopQuery: string = '';

  constructor() { }

  ngOnInit(): void {
  }

  addStop(): void {
    this.stopAdded.emit(this.newStopQuery);
    this.newStopQuery = '';
  }

  removeStop(i: Number): void {
    this.stopRemoved.emit(i);
  }

  toggleOpened(): void {
    if (!this.isOpened) {
      this.isOpened = true;
      document.getElementById('order-menu')?.classList.remove('slide-out');
      document.getElementById('order-menu')?.classList.add('slide-in');
    }
    else {
      setTimeout(() => { this.isOpened = false; }, 300);
      document.getElementById('order-menu')?.classList.remove('slide-in');
      document.getElementById('order-menu')?.classList.add('slide-out');
    }
  }

  getIcon(i: Number) {
    if (i === 0) return this.faCircle;
    else if (i === this.waypoints.length - 1) return this.faFlagCheckered;
    else return this.faStop;
  }
}
