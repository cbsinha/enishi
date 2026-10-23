import { TestBed } from '@angular/core/testing';
import { ClerkService } from 'ngx-clerk';
import { ClerkTokenService } from './clerk-token.service';

describe('ClerkTokenService', () => {
  let service: ClerkTokenService;

  const clerkMock = {
    isLoaded: vi.fn(),
    isSignedIn: vi.fn(),
    getToken: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();

    TestBed.configureTestingModule({
      providers: [
        ClerkTokenService,
        { provide: ClerkService, useValue: clerkMock },
      ],
    });

    service = TestBed.inject(ClerkTokenService);
  });

  it('returns null while Clerk is loading', async () => {
    clerkMock.isLoaded.mockReturnValue(false);
    clerkMock.isSignedIn.mockReturnValue(false);

    await expect(service.getSessionToken()).resolves.toBeNull();
    expect(clerkMock.getToken).not.toHaveBeenCalled();
  });

  it('returns null when the user is signed out', async () => {
    clerkMock.isLoaded.mockReturnValue(true);
    clerkMock.isSignedIn.mockReturnValue(false);

    await expect(service.getSessionToken()).resolves.toBeNull();
    expect(clerkMock.getToken).not.toHaveBeenCalled();
  });

  it('returns the session token when the user is signed in', async () => {
    clerkMock.isLoaded.mockReturnValue(true);
    clerkMock.isSignedIn.mockReturnValue(true);
    clerkMock.getToken.mockResolvedValue('test-session-token');

    await expect(service.getSessionToken()).resolves.toBe(
      'test-session-token',
    );
    expect(clerkMock.getToken).toHaveBeenCalledOnce();
  });
});
