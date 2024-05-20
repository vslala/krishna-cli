default_credentials=$(cat <<EOF
[default]\naws_access_key_id = ${AWS_ACCESS_KEY_ID}\naws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}\naws_session_token = ${AWS_SESSION_TOKEN}\n
EOF
)

echo $default_credentials > ~/.aws/credentials
