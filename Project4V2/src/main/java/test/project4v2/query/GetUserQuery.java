package test.project4v2.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import test.project4v2.Mediator.Mediator.Query;
import test.project4v2.dto.UserDTO;
import test.project4v2.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserQuery implements Query<UserDTO> {
    private User userId;
}
