module.exports = {
  repositoryUrl: 'https://github.com/TheForum-middlefitting/MainService-v2',
  branches: ['main', 'develop'],
  plugins: [
    "@semantic-release/commit-analyzer",
    "@semantic-release/release-notes-generator",
    "@semantic-release/npm",
    {
      "npmPublish": false
    }
  ]
}
