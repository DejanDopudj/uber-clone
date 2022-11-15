import { Component, OnInit } from '@angular/core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  faChevronLeft = faChevronLeft;

  constructor() {
    document.getElementById('register-email')?.focus();
  }

  ngOnInit(): void {
  }

}
