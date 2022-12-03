import { Component, OnInit } from '@angular/core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';

enum AccountType {
  Driver = 'DRIVER',
  Member = 'MEMBER'
}

@Component({
  selector: 'app-profile',
  templateUrl: './account.component.html',
})
export class AccountComponent implements OnInit {
  faChevronLeft = faChevronLeft;
  accountType: AccountType | undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
