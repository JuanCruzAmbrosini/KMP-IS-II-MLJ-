import "./userCard.css"
import { useState } from "react"

type UserCardProps = {
    name: string
    username: string
    avatar: string
    isFollowing?: boolean
}

// Clon de tarjeta de seguidor de Twitter
function UserCard({ name, username, avatar, isFollowing }: UserCardProps) {
    const [following, setFollowing] = useState(isFollowing ?? false)
    return (
        <div className="userCard">
            <img src={avatar} alt={name} />
            <div className="userCardInfo">
                <h2 className="userFullName">{name}</h2>
                <span className="username">{username}</span>
            </div>
            <button
                className={`followButton ${following ? "followButton--following" : ""}`}
                onClick={() => setFollowing(!following)}
            >
                {following ? "Following" : "Follow"}
            </button>
        </div>
  )
}

export default UserCard