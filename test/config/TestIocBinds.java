package config;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import services.AuthenticationService;
import services.EventSourceService;
import services.ImageService;
import services.MessageService;
import services.UserService;
import services.LocationService;
import services.interfaces.IAuthenticationService;
import services.interfaces.IEventSourceService;
import services.interfaces.IImageService;
import services.interfaces.IMessageService;
import services.interfaces.IUserService;
import services.interfaces.ILocationService;

public class TestIocBinds extends AbstractModule{
	
	@Override
	protected void configure() {
		bind(IUserService.class).to(UserService.class);
		bind(IImageService.class).to(ImageService.class);
		bind(ILocationService.class).to(LocationService.class);
		bind(IEventSourceService.class).to(EventSourceService.class).in(Scopes.SINGLETON);
		bind(IMessageService.class).to(MessageService.class);
		bind(IAuthenticationService.class).to(AuthenticationService.class);
		
	}
}
