import { Component, ViewChild } from '@angular/core'
import { NgForm } from '@angular/forms'
import { Router } from '@angular/router'
import { UserService } from '../../services/user.service'
import { RegisterDTO } from '../../dtos/user/register.dto'
import { CommonModule } from '@angular/common'
import { FormsModule } from '@angular/forms'
import { HeaderComponent } from '../header/header.component'
import { FooterComponent } from '../footer/footer.component'

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HeaderComponent,
    FooterComponent
  ]
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm!: NgForm
  // Khai báo các biến tương ứng với các trường dữ liệu trong form
  userName: string
  password: string
  retypePassword: string
  phone_number: string
  fullName: string
  address: string
  isAccepted: boolean
  dateOfBirth: Date
  showPassword: boolean = false

  constructor(private router: Router, private userService: UserService) {
    // debugger
    this.userName = '';
    this.password = '';
    this.retypePassword = '';
    this.phone_number = '';
    this.fullName = '';
    this.address = '';
    this.isAccepted = true;
    this.dateOfBirth = new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 18);
    //inject

  }

  onUserNameChange() {
    console.log(`name typed: ${this.userName}`)
  }

  register() {
    const message = `username: ${this.userName}` +
      `password: ${this.password}` +
      `phone_number: ${this.phone_number}` +
      `retypePassword: ${this.retypePassword}` +
      `address: ${this.address}` +
      `fullName: ${this.fullName}` +
      `isAccepted: ${this.isAccepted}` +
      `dateOfBirth: ${this.dateOfBirth}`
    //alert(message);
    // debugger

    const registerDTO: {
      password: string;
      address: string;
      google_account_id: number;
      role_id: number;
      date_of_birth: Date;
      phone_number: string;
      facebook_account_id: number;
      fullname: string;
      username: string;
      retype_password: string
    } = {
      'fullname': this.fullName,
      'username': this.userName,
      'phone_number': this.phone_number,
      'address': this.address,
      'password': this.password,
      'retype_password': this.retypePassword,
      'date_of_birth': this.dateOfBirth,
      'facebook_account_id': 0,
      'google_account_id': 0,
      'role_id': 1
    }
    this.userService.register(registerDTO).subscribe({
      next: (response: any) => {
        // debugger
        const confirmation = window
          .confirm('OK.')
        if (confirmation) {
          this.router.navigate(['/login'])
        }
      },
      complete: () => {

      },
      error: (error: any) => {
        // debugger
        alert(error?.error?.message ?? '')
      }
    })
  }

  togglePassword() {
    this.showPassword = !this.showPassword
  }

  //how to check password match ?
  checkPasswordsMatch() {
    if (this.password !== this.retypePassword) {
      this.registerForm.form.controls['retypePassword']
        .setErrors({'passwordMismatch': true})
    } else {
      this.registerForm.form.controls['retypePassword'].setErrors(null)
    }
  }

  checkAge() {
    if (this.dateOfBirth) {
      const today = new Date()
      const birthDate = new Date(this.dateOfBirth)
      let age = today.getFullYear() - birthDate.getFullYear()
      const monthDiff = today.getMonth() - birthDate.getMonth()
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--
      }

      if (age < 18) {
        this.registerForm.form.controls['dateOfBirth'].setErrors({'invalidAge': true})
      } else {
        this.registerForm.form.controls['dateOfBirth'].setErrors(null)
      }
    }
  }
}

