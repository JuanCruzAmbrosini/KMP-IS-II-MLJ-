import "./styles.css"

import UserCard from './components/userCard'

function App() {
  return (
    <div className="App">
      <h1>React Test</h1>
      <UserCard name="Darren Watkins" username="@dwatkins" avatar="https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZjFkejd1ZHppYnYwaTlzaDJ0Nnp6d2J1OHBmbWV5MnB4ZnB3dmIwNSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/D63HGAzG15LQrjBPRE/giphy.gif" />
      <UserCard name="Speed" username="@ishowspeed" avatar="https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3dmsxcXowMGY4Nmptd203Nms2c3N3MDF6dmU0OGJ0Y3NhdmhkMHhsNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/oPvWTJmebjD0KqjUnT/giphy.gif" isFollowing={true} />
      <UserCard name="Cristiano Ronaldo Sui" username="@cr7sui" avatar="https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3dmsxcXowMGY4Nmptd203Nms2c3N3MDF6dmU0OGJ0Y3NhdmhkMHhsNyZlcD12MV9naWZzX3NlYXJjaCZjdD1n/fkAj7cN0OEuHXOXFoQ/giphy.gif" />
    </div>
  )
}

export default App