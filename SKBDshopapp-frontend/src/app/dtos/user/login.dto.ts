import {
  IsString,
  IsNotEmpty,

  IsDate
} from 'class-validator'

export class LoginDTO {
  user_name: string

  @IsString()
  @IsNotEmpty()
  password: string

  role_id: number

  constructor(data: any) {
    this.user_name = data.user_name
    this.password = data.password
    this.role_id = data.role_id
  }
}
