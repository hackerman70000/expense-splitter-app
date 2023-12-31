package pl.edu.pw.api.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.auth.dto.RegisterDTO;
import pl.edu.pw.api.auth.dto.UserTokenDTO;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.models.User;

import java.io.IOException;


@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector("1");

	@PostMapping("/register")
	public UserTokenDTO register(@RequestBody @Validated RegisterDTO registerDTO,HttpServletResponse response) throws IOException {
		if(dbc.findUserByName(registerDTO.getUsername())==null) { //Check if username already exists
			dbc.addUser(new User(registerDTO.getUsername(), registerDTO.getPassword()));
			UserTokenDTO utdto = new UserTokenDTO();
			utdto.setToken(jwtService.generateToken(registerDTO.getUsername()));
			utdto.setUserId(dbc.findUserByName(registerDTO.getUsername()).getId());
			return utdto;
		}else {
			response.getWriter().print("This username already exists");
		}
		response.setStatus(401);
		return null;
	}

	@PostMapping("/login")
	public UserTokenDTO authenticateAndGetToken(@RequestBody LoginDTO loginDTO, HttpServletResponse response) throws IOException {
		if(dbc.findUserByName(loginDTO.getUsername())!=null) {
			User user = new User(loginDTO.getUsername(), loginDTO.getPassword());
			if (user.passwordCompare(loginDTO.getPassword(),dbc.findUserByName(loginDTO.getUsername()).getPasswordHash())) {
				UserTokenDTO utdto = new UserTokenDTO();
				utdto.setToken(jwtService.generateToken(loginDTO.getUsername()));
				utdto.setUserId(dbc.findUserByName(loginDTO.getUsername()).getId());
				return utdto;
			}else {
				response.getWriter().print("Wrong username or password");
			}
		}else {
			response.getWriter().print("Wrong username or password");
		}
		response.setStatus(401);
		return null;
	}

//	@GetMapping("/logout")
//	public void logout() {
	// TODO: On APP side --> Delete active token
//	}
}
