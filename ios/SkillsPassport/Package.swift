// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "SkillsPassport",
    platforms: [.iOS(.v16), .macOS(.v13)],
    products: [
        .library(name: "SkillsPassport", targets: ["SkillsPassport"]),
    ],
    targets: [
        .target(name: "SkillsPassport"),
        .testTarget(name: "SkillsPassportTests", dependencies: ["SkillsPassport"]),
    ]
)
