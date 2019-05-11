workflow "VoteBot" {
  on = "push"
  resolves = ["MrRamych/gradle-actions/openjdk-12@2.1"]
}

action "GitHub Action for Maven" {
  uses = "MrRamych/gradle-actions/openjdk-12@2.1"
  args = "compileJava compileKotlin"
}

action "Continue with Release procedure when tag" {
  uses = "actions/bin/filter@3c0b4f0e63ea54ea5df2914b4fabf383368cd0da"
  needs = ["GitHub Action for Maven"]
  args = "tag"
}

action "MrRamych/gradle-actions/openjdk-12@2.1" {
  uses = "MrRamych/gradle-actions/openjdk-12@2.1"
  needs = ["Continue with Release procedure when tag"]
  args = "jar shadowJar"
}
