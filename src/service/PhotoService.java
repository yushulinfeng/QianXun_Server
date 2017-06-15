package service;

import java.io.File;
import java.util.Map;

import domain.User;

public interface PhotoService {
	public Map<String, Object> add(User user, File photo);
}
