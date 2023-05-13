package pl.edu.pw.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.auth.dto.RegisterDTO;
import pl.edu.pw.api.auth.dto.UserTokenDTO;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.models.User;


@RestController("/auth")
public class AuthController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector();

	@PostMapping("/register")
	public UserTokenDTO register(@RequestBody @Validated RegisterDTO registerDTO) {
		if(dbc.findUserByName(registerDTO.getUsername())==null) { //Check if username already exists
			dbc.addUser(new User(registerDTO.getUsername(), registerDTO.getPassword()));
			UserTokenDTO utdto = new UserTokenDTO();
			utdto.setToken(jwtService.generateToken(registerDTO.getUsername()));
			utdto.setUserId(dbc.findUserByName(registerDTO.getUsername()).getId());
			return utdto;
		}
		return null;
	}

	@PostMapping("/login")
	public String authenticateAndGetToken(@RequestBody LoginDTO loginDTO) {
		if(dbc.findUserByName(loginDTO.getUsername())!=null) {
			User user = new User(loginDTO.getUsername(), loginDTO.getPassword());
			if (user.passwordCompare(loginDTO.getPassword(),dbc.findUserByName(loginDTO.getUsername()).getPasswordHash())) {
				return jwtService.generateToken(loginDTO.getUsername());
			} else {
				return null;
			}
		}
		return null;
	}


//	@GetMapping("/logout")
//	public void logout() {
		// TODO: On APP side --> Delete active token
//	}

	@GetMapping("/test")
	public LoginDTO test1() {
		LoginDTO login = new LoginDTO();
		login.setUsername("jan");
		login.setPassword("PassWord");
		return login;
	}
	@GetMapping("/test/{id}")
	public @ResponseBody String generateReport(@PathVariable("id") Long id, HttpServletRequest request) {

		if(jwtService.checkUserToken(id, request)){
			return "correct " + jwtService.getUsernameFromToken(id,request);
		}
		return "wrong";
	}
}
