import {Component, ViewChild, OnInit, viewChild} from '@angular/core';
import { LoginDTO } from '../../dtos/user/login.dto';
import { UserService } from '../../services/user.service';
import { TokenService } from '../../services/token.service';
import { RoleService } from '../../services/role.service';
import { Router, ActivatedRoute } from '@angular/router';
import { NgForm } from '@angular/forms';
import { LoginResponse } from '../../responses/user/login.response';
import { Role } from '../../models/role';
import { UserResponse } from '../../responses/user/user.response';
import { CartService } from '../../services/cart.service';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    HeaderComponent,
    FooterComponent,
    CommonModule,
    FormsModule
  ]
})
export class LoginComponent implements OnInit {
  @ViewChild('loginForm') loginForm!: NgForm;
  userName: string = '';
  password: string = '';
  showPassword: boolean = false;

  roles: Role[] = [];
  rememberMe: boolean = true;
  selectedRole?: Role;
  userResponse?: UserResponse;


  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private tokenService: TokenService,
    private roleService: RoleService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    this.loadRoles();
  }

  loadRoles() {
    this.roleService.getRoles().subscribe({
      next: (roles: Role[]) => {
        this.roles = roles;
        this.selectedRole = roles.length > 0 ? roles[0] : undefined;
      },
      error: (error: any) => {
        console.error('Error getting roles:', error);
      }
    });
  }

  onUserNameChange() {
    // Validate that phone number is at least 6 characters
    if (this.userName.length < 6) {
      console.warn('Phone number must be at least 6 characters');
    }
  }

  createAccount() {
    this.router.navigate(['/register']);
  }

  login() {
    const loginDTO: LoginDTO = {
      user_name: this.userName,
      password: this.password,
      role_id: this.selectedRole?.id ?? 1
    };

    this.userService.login(loginDTO).subscribe({
      next: (response: LoginResponse) => {
        const { token } = response;
        if (this.rememberMe) {
          this.tokenService.setToken(token);
          this.fetchUserDetail(token);
        }
      },
      error: (error: any) => {
        alert(error.error.message);
      }
    });
  }

  fetchUserDetail(token: string) {
    this.userService.getUserDetail(token).subscribe({
      next: (response: any) => {
        this.userResponse = {
          ...response,
          date_of_birth: new Date(response.date_of_birth)
        };
        this.userService.saveUserResponseToLocalStorage(this.userResponse);

        if (this.userResponse?.role.name === 'admin') {
          this.router.navigate(['/admin']);
        } else if (this.userResponse?.role.name === 'user') {
          this.router.navigate(['/']);
        }
      },
      error: (error: any) => {
        alert(error.error.message);
      },
      complete: () => {
        this.cartService.refreshCart();
      }
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
}
