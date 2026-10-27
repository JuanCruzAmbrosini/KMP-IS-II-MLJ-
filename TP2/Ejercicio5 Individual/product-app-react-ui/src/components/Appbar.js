import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import { useAuth } from './AuthContext';
import { useNavigate } from 'react-router-dom';

export default function ButtonAppBar() {

  const {isAuthenticated, logout} = useAuth()

  const navigate = useNavigate();

  const logoutUser = () => {
    logout()
    navigate("/login")
  }

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h4" component="div" sx={{ flexGrow: 1 }}>
            Products List
          </Typography>
          {isAuthenticated ? (
            <Button onClick={logoutUser} color="inherit">Logout</Button>
          ) : (
            <Button onClick={() => navigate('/login')} color="inherit">Login</Button>
          )}
          <a href='/registration'>REGISTER</a>
        </Toolbar>
      </AppBar>
    </Box>
  );
}
