import { Component, OnInit } from '@angular/core';
import { IconDefinition, faChevronRight, faChevronLeft } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-order-menu',
  templateUrl: './order-menu.component.html',
  styleUrls: ['./order-menu.component.css']
})
export class OrderMenuComponent implements OnInit {
  faChevronRight: IconDefinition = faChevronRight;
  faChevronLeft: IconDefinition = faChevronLeft;

  isOpened: boolean = false;

  constructor() { }

  ngOnInit(): void {
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

}
