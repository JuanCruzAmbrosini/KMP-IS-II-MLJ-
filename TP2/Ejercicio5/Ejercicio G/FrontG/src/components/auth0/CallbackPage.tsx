import { useAuth0 } from "@auth0/auth0-react";

const CallbackPage = () => {
  const { error, isLoading } = useAuth0();

  return (
    <main className="flex min-h-screen items-center justify-center px-6">
      <p className="max-w-xl text-center text-lg text-gray-600">
        {error
          ? `No se pudo completar el inicio de sesión: ${error.message}`
          : isLoading
          ? "Completando el inicio de sesión..."
          : "Redirigiendo..."}
      </p>
    </main>
  );
};

export default CallbackPage;
