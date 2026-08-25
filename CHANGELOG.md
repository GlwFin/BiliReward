# Changelog

All notable changes to BiliReward are documented in this file.

## [v1.1] - 2026-08-26

### Added
- **Auto-close ad**: the rewarded-ad activity is finished immediately after
  creation, so the ad content never becomes visible.
- **Active reward grant**: the close/grant method is invoked programmatically
  before the activity closes, so the reward is still issued even though the
  ad is not shown.

### Changed
- `onCreate` hook now actively triggers the reward path instead of only
  setting flags.

## [v1.0] - 2026-08-26

### Added
- Initial release.
- Version-agnostic detection: finds the reward-ad Activity by the presence
  of a `RewardAdListener` field (type signature, not hard-coded names).
- Sets all boolean fields to `true` after `onCreate`.
- Returns `true` for every no-arg boolean method (gate checks).
- Forces boolean arguments to `true` in methods taking `RewardAdCloseFrom`.
- Sets all boolean fields to `true` on `finish()`.
- Supports Bilibili 8.49.0 ~ 9.0.0+ (and any release that keeps the
  `RewardAdListener` field).
