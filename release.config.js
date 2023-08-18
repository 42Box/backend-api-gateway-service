module.exports = {
  repositoryUrl: 'https://github.com/42Box/backend-api-gateway-service',
  branches: ['main'],
  plugins: [
    "@semantic-release/commit-analyzer",
    "@semantic-release/release-notes-generator",
    "@semantic-release/npm",
    {
      "npmPublish": false
    }
  ]
}
