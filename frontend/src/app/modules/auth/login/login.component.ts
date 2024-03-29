import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { AuthenticationService } from 'src/app/core/authentication/authentication.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
})
export class LoginComponent {
  faChevronLeft: IconDefinition = faChevronLeft;
  errorMessage: string = '';

  showPasswordChangeResponseModal: boolean = false;
  passwordChangeResponseModalTitle: string = '';
  passwordChangeResponseModalMessage: string = '';

  loginForm = new FormGroup({
    email: new FormControl('', [
      Validators.required,
      Validators.pattern(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/)
    ]),
    password: new FormControl('', [
      Validators.required
    ]),
  });

  constructor(private authenticationService: AuthenticationService) { 
    document.getElementById('login-email')?.focus();
  }

  resetPassword(): void {
    if (!this.email?.value)
      this.errorMessage = 'You must enter your email first.';
    else if (this.email?.invalid)
      this.errorMessage = 'Enter a valid email.';
    else {
      this.authenticationService.resetPasword(this.email?.value!)
      .then(res => {
        if (res.data) {
          this.passwordChangeResponseModalTitle = 'Success!';
          this.passwordChangeResponseModalMessage = 'An email was sent to your address.';
          this.showPasswordChangeResponseModal = true;
        } else {
          this.passwordChangeResponseModalTitle = 'Something went wrong.';
          this.passwordChangeResponseModalMessage = 'Please check your email and try again.';
          this.showPasswordChangeResponseModal = true;
        }
      })
      .catch(err => {
        this.passwordChangeResponseModalTitle = 'Something went wrong.';
        this.passwordChangeResponseModalMessage = 'Please check your email and try again.';
        this.showPasswordChangeResponseModal = true;
      });
    }
  }

  async onSubmit() {
    if (this.loginForm.invalid) return;
    const success = await this.authenticationService.login(this.email?.value!, this.password?.value!);
    if (success) {   
      this.authenticationService.whoami();
    }
    else {
      this.errorMessage = 'Incorrect credentials.';
    }
  }

  get email() {
    return this.loginForm.get('email');
  }

  get password() {
    return this.loginForm.get('password');
  }

  goHome(): void {
    window.location.href = '/';
  }

  closePasswordChangeResponseModal(): void {
    this.passwordChangeResponseModalTitle = '';
    this.passwordChangeResponseModalMessage = '';
    this.showPasswordChangeResponseModal = false;
  }
}
