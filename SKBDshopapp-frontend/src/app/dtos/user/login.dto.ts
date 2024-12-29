import {
  IsString,
  IsNotEmpty,

  IsDate
} from 'class-validator'

export class LoginDTO {
    @IsString()
    @IsNotEmpty()
    username: string

  @IsString()
  @IsNotEmpty()
  password: string

  role_id: number

  constructor(data: any) {
    this.username = data.username
    this.password = data.password
    this.role_id = data.role_id
  }
}
